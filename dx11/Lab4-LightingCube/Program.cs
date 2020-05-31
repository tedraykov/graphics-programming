using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Core;
using Core.Vertex;
using SlimDX;
using SlimDX.D3DCompiler;
using SlimDX.Direct3D11;
using SlimDX.DXGI;
using Buffer = SlimDX.Direct3D11.Buffer;
using Debug = System.Diagnostics.Debug;

namespace Lab_4_1
{
    using Effect = Effect;

    public class LightingCube : D3DApp
    {
        private Buffer _cubeVB;
        private Buffer _cubeIB;

        private readonly Color4 _ambientLight;
        private SpotLight _spotLight;

        private readonly List<Material> _cubeMaterials;
        private readonly Dictionary<string, Vector3> _cubeNormals;

        private Effect _fx;
        private EffectTechnique _tech;
        private EffectMatrixVariable _fxWVP;
        private EffectVectorVariable _fxEyePosW;
        private EffectVariable _fxAmbientLight;
        private EffectVariable _fxSpotLight;

        private InputLayout _inputLayout;


        // Matrices
        private Matrix _world;
        private Matrix _view;
        private Matrix _proj;

        // Camera variables
        private float _theta;
        private float _phi;
        private float _radius;
        private Vector3 _eyePosW;
        
        private Point _lastMousePos;

        private bool _disposed;

        public LightingCube(IntPtr hInstance) : base(hInstance)
        {
            _cubeIB = null;
            _cubeVB = null;
            _fx = null;
            _tech = null;
            _fxWVP = null;
            _fxAmbientLight = null;
            _fxSpotLight = null;
            _fxEyePosW = null;
            _inputLayout = null;
            _theta = 1.5f * MathF.PI;
            _phi = 0.25f * MathF.PI;
            _radius = 7.0f;
            _eyePosW = new Vector3();

            MainWindowCaption = "Lighting Cube";
            _lastMousePos = new Point(0, 0);
            _world = Matrix.Identity;
            _view = Matrix.Identity;
            _proj = Matrix.Identity;

            _ambientLight = new Color4(1.0f, 0.5f, 0.5f, 0.5f);

            _spotLight = new SpotLight {
                Ambient = new Color4(0,0,0),
                Diffuse = new Color4(1.0f, 1.0f, 1.0f),
                Specular = Color.White,
                Attenuation = new Vector3(1.0f, 0.0f, 0.0f),
                Spot = 96.0f,
                Position = new Vector3(10f, 10f, 10f),
                Range = 10000.0f
            };

            _cubeMaterials = new List<Material>
            {
                new Material
                {
                    Ambient = new Color4(1.0f, 0.1f, 0.1f, 0.5f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                },
                new Material
                {
                    Ambient = new Color4(1.0f, 0.1f, 0.5f, 0.1f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                },
                new Material
                {
                    Ambient = new Color4(1.0f, 0.5f, 0.1f, 0.1f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                },
                new Material
                {
                    Ambient = new Color4(1.0f, 0.1f, 0.5f, 0.5f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                },
                new Material
                {
                    Ambient = new Color4(1.0f, 0.5f, 0.1f, 0.5f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                },
                new Material
                {
                    Ambient = new Color4(1.0f, 0.5f, 0.5f, 0.1f),
                    Diffuse = new Color4(1.0f, 0.7f, 0.7f, 0.7f),
                    Specular = new Color4(16.0f, 0.9f, 0.9f, 0.9f)
                }
            };

            _cubeNormals = new Dictionary<string, Vector3>
            {
                {"back", new Vector3(0, 0, -1)},
                {"front", new Vector3(0, 0, 1)},
                {"right", new Vector3(-1, 0, 0)},
                {"left", new Vector3(1, 0, 0)},
                {"top", new Vector3(0, 1, 0)},
                {"bottom", new Vector3(0, -1, 0)},
            };
        }

        protected override void Dispose(bool disposing)
        {
            if (!_disposed)
            {
                if (disposing)
                {
                    Util.ReleaseCom(ref _cubeVB);
                    Util.ReleaseCom(ref _cubeIB);
                    Util.ReleaseCom(ref _fx);
                    Util.ReleaseCom(ref _inputLayout);
                }

                _disposed = true;
            }

            base.Dispose(disposing);
        }

        public override bool Init()
        {
            if (!base.Init())
            {
                return false;
            }

            BuildGeometryBuffers();
            BuildFX();
            BuildVertexLayout();

            return true;
        }

        public override void OnResize()
        {
            base.OnResize();
            // Recalculate perspective matrix
            _proj = Matrix.PerspectiveFovLH(0.25f * MathF.PI, AspectRatio, 1.0f, 1000.0f);
        }

        public override void UpdateScene(float dt)
        {
            base.UpdateScene(dt);

            // Get camera position from polar coords
            var x = _radius * MathF.Sin(_phi) * MathF.Cos(_theta);
            var z = _radius * MathF.Sin(_phi) * MathF.Sin(_theta);
            var y = _radius * MathF.Cos(_phi);
            _eyePosW = new Vector3(x,y,z);
            
            // Build the view matrix
            var target = new Vector3(0);
            var up = new Vector3(0, 1, 0);
            _view = Matrix.LookAtLH(_eyePosW, target, up);

            _spotLight.Position = _eyePosW;
            _spotLight.Direction = Vector3.Normalize(target - _spotLight.Position);
        }

        public override void DrawScene()
        {
            base.DrawScene();
            ImmediateContext.ClearRenderTargetView(RenderTargetView, Color.DarkGray);
            ImmediateContext.ClearDepthStencilView(DepthStencilView,
                DepthStencilClearFlags.Depth | DepthStencilClearFlags.Stencil, 1.0f, 0);

            ImmediateContext.InputAssembler.InputLayout = _inputLayout;
            ImmediateContext.InputAssembler.PrimitiveTopology = PrimitiveTopology.TriangleList;

            var wvp = _world * _view * _proj;
            _fxWVP.SetMatrix(wvp);
            
            // Lighting
            var array = Util.GetArray(_ambientLight);
            _fxAmbientLight.SetRawValue(
                new DataStream(array, false, false), Marshal.SizeOf(_ambientLight)
            );
            array = Util.GetArray(_spotLight);
            _fxSpotLight.SetRawValue(
                new DataStream(array, false, false), Marshal.SizeOf(_spotLight) 
            );
            _fxEyePosW.Set(_eyePosW);
            
            ImmediateContext.InputAssembler.SetVertexBuffers(0,
                new VertexBufferBinding(_cubeVB, VertexPMN.Stride, 0));
            ImmediateContext.InputAssembler.SetIndexBuffer(_cubeIB, Format.R32_UInt, 0);

            for (int p = 0; p < _tech.Description.PassCount; p++)
            {
                _tech.GetPassByIndex(p).Apply(ImmediateContext);
                ImmediateContext.DrawIndexed(36, 0, 0);
            }

            SwapChain.Present(0, PresentFlags.None);
        }

        protected override void OnMouseDown(object sender, MouseEventArgs mouseEventArgs)
        {
            _lastMousePos = mouseEventArgs.Location;
            Window.Capture = true;
        }

        protected override void OnMouseUp(object sender, MouseEventArgs e)
        {
            Window.Capture = false;
        }

        protected override void OnMouseMove(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                var dx = MathF.ToRadians(0.25f * (e.X - _lastMousePos.X));
                var dy = MathF.ToRadians(0.25f * (e.Y - _lastMousePos.Y));

                _theta += dx;
                _phi += dy;

                _phi = MathF.Clamp(_phi, 0.1f, MathF.PI - 0.1f);
            }
            else if (e.Button == MouseButtons.Right)
            {
                var dx = 0.005f * (e.X - _lastMousePos.X);
                var dy = 0.005f * (e.Y - _lastMousePos.Y);
                _radius += dx - dy;

                _radius = MathF.Clamp(_radius, 3.0f, 15.0f);
            }

            _lastMousePos = e.Location;
        }

        private void BuildGeometryBuffers()
        {
            var vertices = new[]
            {
                // Back
                new VertexPMN(new Vector3(-1, -1, -1), _cubeMaterials[0], _cubeNormals["back"]),
                new VertexPMN(new Vector3(-1, 1, -1), _cubeMaterials[0], _cubeNormals["back"]),
                new VertexPMN(new Vector3(1, -1, -1), _cubeMaterials[0], _cubeNormals["back"]),
                new VertexPMN(new Vector3(1, 1, -1), _cubeMaterials[0], _cubeNormals["back"]),

                // Front
                new VertexPMN(new Vector3(-1, -1, 1), _cubeMaterials[1], _cubeNormals["front"]),
                new VertexPMN(new Vector3(-1, 1, 1), _cubeMaterials[1], _cubeNormals["front"]),
                new VertexPMN(new Vector3(1, -1, 1), _cubeMaterials[1], _cubeNormals["front"]),
                new VertexPMN(new Vector3(1, 1, 1), _cubeMaterials[1], _cubeNormals["front"]),

                // Left
                new VertexPMN(new Vector3(-1, -1, -1), _cubeMaterials[2], _cubeNormals["left"]),
                new VertexPMN(new Vector3(-1, 1, -1), _cubeMaterials[2], _cubeNormals["left"]),
                new VertexPMN(new Vector3(-1, -1, 1), _cubeMaterials[2], _cubeNormals["left"]),
                new VertexPMN(new Vector3(-1, 1, 1), _cubeMaterials[2], _cubeNormals["left"]),

                // Right
                new VertexPMN(new Vector3(1, -1, -1), _cubeMaterials[3], _cubeNormals["right"]),
                new VertexPMN(new Vector3(1, 1, -1), _cubeMaterials[3], _cubeNormals["right"]),
                new VertexPMN(new Vector3(1, -1, 1), _cubeMaterials[3], _cubeNormals["right"]),
                new VertexPMN(new Vector3(1, 1, 1), _cubeMaterials[3], _cubeNormals["right"]),

                //Top
                new VertexPMN(new Vector3(-1, 1, -1), _cubeMaterials[4], _cubeNormals["top"]),
                new VertexPMN(new Vector3(1, 1, -1), _cubeMaterials[4], _cubeNormals["top"]),
                new VertexPMN(new Vector3(-1, 1, 1), _cubeMaterials[4], _cubeNormals["top"]),
                new VertexPMN(new Vector3(1, 1, 1), _cubeMaterials[4], _cubeNormals["top"]),
                //
                // // Bottom
                new VertexPMN(new Vector3(-1, -1, -1), _cubeMaterials[5], _cubeNormals["bottom"]),
                new VertexPMN(new Vector3(1, -1, -1), _cubeMaterials[5], _cubeNormals["bottom"]),
                new VertexPMN(new Vector3(-1, -1, 1), _cubeMaterials[5], _cubeNormals["bottom"]),
                new VertexPMN(new Vector3(1, -1, 1), _cubeMaterials[5], _cubeNormals["bottom"]),
            };

            var vbd = new BufferDescription(
                VertexPMN.Stride * vertices.Length,
                ResourceUsage.Immutable,
                BindFlags.VertexBuffer,
                CpuAccessFlags.None,
                ResourceOptionFlags.None,
                0);
            _cubeVB = new Buffer(Device, new DataStream(vertices, true, false), vbd);

            var indices = new uint[]
            {
                //back
                0, 1, 2,
                1, 3, 2,
                //front
                4, 6, 5,
                5, 6, 7,
                //left
                8, 10, 9,
                9, 10, 11,
                //right
                12, 13, 14,
                13, 15, 14,
                //top
                16, 18, 17,
                17, 18, 19,
                //bottom
                20, 21, 22,
                21, 23, 22
            };
            var ibd = new BufferDescription(
                sizeof(uint) * indices.Length,
                ResourceUsage.Immutable,
                BindFlags.IndexBuffer,
                CpuAccessFlags.None,
                ResourceOptionFlags.None,
                0);
            _cubeIB = new Buffer(Device, new DataStream(indices, false, false), ibd);
        }

        private void BuildFX()
        {
            var shaderFlags = ShaderFlags.None;
#if DEBUG
            shaderFlags |= ShaderFlags.Debug;
            shaderFlags |= ShaderFlags.SkipOptimization;
#endif
            string errors = null;
            ShaderBytecode compiledShader = null;
            try
            {
                compiledShader = ShaderBytecode.CompileFromFile(
                    "FX/color.fx",
                    null,
                    "fx_5_0",
                    shaderFlags,
                    EffectFlags.None,
                    null,
                    null,
                    out errors);
                _fx = new Effect(Device, compiledShader);
            }
            catch (Exception ex)
            {
                if (!string.IsNullOrEmpty(errors))
                {
                    MessageBox.Show(errors);
                }

                MessageBox.Show(ex.Message);
                return;
            }
            finally
            {
                Util.ReleaseCom(ref compiledShader);
            }

            _tech = _fx.GetTechniqueByName("ColorTech");
            _fxWVP = _fx.GetVariableByName("gWorldViewProj").AsMatrix();
            _fxAmbientLight = _fx.GetVariableByName("gAmbientLight");
            _fxSpotLight = _fx.GetVariableByName("gSpotLight");
            _fxEyePosW = _fx.GetVariableByName("gEyePosW").AsVector();
        }

        private void BuildVertexLayout()
        {
            var vertexDesc = new[]
            {
                new InputElement("POSITION", 0, Format.R32G32B32_Float,
                    0, 0, InputClassification.PerVertexData, 0),
                new InputElement("MATERIAL", 0, Format.R32G32B32A32_Float,
                    InputElement.AppendAligned, 0, InputClassification.PerVertexData, 0),
                new InputElement("MATERIAL", 1, Format.R32G32B32A32_Float,
                    InputElement.AppendAligned, 0, InputClassification.PerVertexData, 0),
                new InputElement("MATERIAL", 2, Format.R32G32B32A32_Float,
                    InputElement.AppendAligned, 0, InputClassification.PerVertexData, 0),
                new InputElement("MATERIAL", 3, Format.R32G32B32A32_Float,
                    InputElement.AppendAligned, 0, InputClassification.PerVertexData, 0),
                new InputElement("NORMAL", 0, Format.R32G32B32_Float,
                    InputElement.AppendAligned, 0, InputClassification.PerVertexData, 0)
            };
            Debug.Assert(_tech != null);
            var passDesc = _tech.GetPassByIndex(0).Description;
            _inputLayout = new InputLayout(Device, passDesc.Signature, vertexDesc);
        }
    }


    static class Program
    {
        static void Main(string[] args)
        {
            Configuration.EnableObjectTracking = true;
            var app = new LightingCube(Process.GetCurrentProcess().Handle);
            if (!app.Init())
            {
                return;
            }

            app.Run();
        }
    }
}
