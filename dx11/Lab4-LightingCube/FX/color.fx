struct SpotLight {
	float4 Ambient;
	float4 Diffuse;
	float4 Specular;

	float3 Position;
	float Range;

	float3 Direction;
	float Spot;

	float3 Att;
	float pad;
};

cbuffer cbPerFrame {
	SpotLight gSpotLight;
	float3 gEyePosW;
    float4 gAmbientLight;
};

cbuffer cbPerObject {
	float4x4 gWorldViewProj;
}

struct Material {
	float4 Ambient;
	float4 Diffuse;
	float4 Specular; // w = SpecPower
	float4 Reflect;
};

struct VertexIn {
	float3 PosL : POSITION;
	Material MaterialF: MATERIAL;
    float3 NormalL: NORMAL;
};

struct VertexOut {
	float4 PosW: SV_POSITION;
    Material MaterialF: MATERIAL;
    float4 NormalW: NORMAL;
};

// Equation 7.5
void ComputeSpotLight(Material mat, SpotLight L, float3 pos, float3 normal, float3 toEye,
out float4 ambient, out float4 diffuse, out float4 spec)
{
    // Initialize outputs.
    ambient = float4(0.0f, 0.0f, 0.0f, 0.0f);
    diffuse = float4(0.0f, 0.0f, 0.0f, 0.0f);
    spec = float4(0.0f, 0.0f, 0.0f, 0.0f);
    // The vector from the surface to the light.
    float3 lightVec = L.Position - pos;
    // The distance from surface to light.
    float d = length(lightVec);
    // Range test.
    if( d > L.Range )
        return;
    // Normalize the light vector.
    lightVec /= d;
    // Ambient term.
    ambient = mat.Ambient * L.Ambient;
    // Add diffuse and specular term, provided the surface is in
    // the line of site of the light.
    float diffuseFactor = dot(lightVec, normal);
    // Flatten to avoid dynamic branching.
    [flatten]
    if(diffuseFactor > 0.0f)
    {
        float3 v = reflect(-lightVec, normal);
        float specFactor = pow(max(dot(v, toEye), 0.0f), mat.Specular.w);
        diffuse = diffuseFactor * mat.Diffuse * L.Diffuse;
        spec = specFactor * mat.Specular * L.Specular;
    }
    // Scale by spotlight factor and attenuate.
    float spot = pow(max(dot(-lightVec, L.Direction), 0.0f), L.Spot);
    // Scale by spotlight factor and attenuate.
    float att = spot / dot(L.Att, float3(1.0f, d, d*d));
    ambient *= spot;
    diffuse *= att;
    spec *= att;
}


VertexOut VS(VertexIn vin) {
	VertexOut vout;
	vout.PosW = mul(float4(vin.PosL, 1.0f), gWorldViewProj);
	vout.MaterialF = vin.MaterialF;
	vout.NormalW = mul(float4(vin.NormalL, 1.0f), gWorldViewProj);
	return vout;
}

float4 PS(VertexOut pin) :SV_Target {
    // Interpolating normal can unnormalize it, so normalize it.
    pin.NormalW = normalize(pin.NormalW);
    float3 toEyeW = normalize(gEyePosW - pin.PosW.xyz);
    
    // Start with a sum of zero. 
	float4 ambient = float4(0.0f, 0.0f, 0.0f, 0.0f);
	float4 diffuse = float4(0.0f, 0.0f, 0.0f, 0.0f);
	float4 spec    = float4(0.0f, 0.0f, 0.0f, 0.0f);

    // Calculate ambient light
    ambient += pin.MaterialF.Ambient * gAmbientLight;
	// Sum the light contribution from each light source.
	float4 A, D, S;
    
    ComputeSpotLight(pin.MaterialF, gSpotLight,
    pin.PosW.xyz, pin.NormalW.xyz, toEyeW, A, D, S);
    
    ambient += A;
    diffuse += D;
    spec += S;
    
    float4 litColor = ambient + diffuse + spec;
    litColor.a = pin.MaterialF.Diffuse.a;
     
    return litColor;
}

technique11 ColorTech {
	pass P0{
		SetVertexShader( CompileShader( vs_4_0, VS()));
		SetGeometryShader(NULL);
		SetPixelShader(CompileShader( ps_4_0, PS()));
	}
}
