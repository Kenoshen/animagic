#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform bool isLight0;
uniform bool isLight1;
uniform bool isLight2;
uniform bool isLight3;
uniform bool isLight4;
uniform bool isLight5;
uniform bool isLight6;
uniform bool isLight7;
uniform bool isLight8;
uniform bool isLight9;
uniform vec3 light0;
uniform vec3 light1;
uniform vec3 light2;
uniform vec3 light3;
uniform vec3 light4;
uniform vec3 light5;
uniform vec3 light6;
uniform vec3 light7;
uniform vec3 light8;
uniform vec3 light9;
uniform vec3 lightColor0;
uniform vec3 lightColor1;
uniform vec3 lightColor2;
uniform vec3 lightColor3;
uniform vec3 lightColor4;
uniform vec3 lightColor5;
uniform vec3 lightColor6;
uniform vec3 lightColor7;
uniform vec3 lightColor8;
uniform vec3 lightColor9;
uniform float attenuation0;
uniform float attenuation1;
uniform float attenuation2;
uniform float attenuation3;
uniform float attenuation4;
uniform float attenuation5;
uniform float attenuation6;
uniform float attenuation7;
uniform float attenuation8;
uniform float attenuation9;
uniform float ambientIntensity;
uniform vec3 ambientColor;
uniform bool useNormals;
uniform bool drawNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;

uniform float xCoordMin;
uniform float xCoordDiff;
uniform float yCoordMin;
uniform float yCoordDiff;
uniform float xNorCoordMin;
uniform float xNorCoordDiff;
uniform float yNorCoordMin;
uniform float yNorCoordDiff;

vec3 calculateLight(
                    bool isLight,
                    vec3 light,
                    vec3 lightColor,
                    float attenuation,
                    vec2 position,
                    vec4 textureColor,
                    vec3 normal,
                    vec4 vertexColor,
                    bool useShadow
                    );

void main() {
    vec2 texCoords = v_texCoords.xy;
    vec2 norCoords = vec2(xNorCoordMin + (((texCoords.x - xCoordMin) / xCoordDiff) * xNorCoordDiff),
                          yNorCoordMin + (((texCoords.y - yCoordMin) / yCoordDiff) * yNorCoordDiff));
    vec4 color = texture2D(u_texture, v_texCoords);
    vec3 nColor = texture2D(u_normals, norCoords).rgb;
    //some bump map programs will need the Y value flipped..
    nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;
    //this is for debugging purposes, allowing us to lower the intensity of our bump map
    vec3 nBase = vec3(1.0, 1.0, 1.0);
    nColor = mix(nBase, nColor, strength);
    //normals need to be converted to [-1.0, 1.0] range and normalized
    vec3 normal = normalize(nColor * 2.0 - 1.0);
    vec3 ambient = vec3(ambientColor * ambientIntensity);
    if (useNormals){
        vec3 additiveBlending =
            ambient +
            calculateLight(isLight0, light0, lightColor0, attenuation0, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight1, light1, lightColor1, attenuation1, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight2, light2, lightColor2, attenuation2, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight3, light3, lightColor3, attenuation3, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight4, light4, lightColor4, attenuation4, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight5, light5, lightColor5, attenuation5, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight6, light6, lightColor6, attenuation6, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight7, light7, lightColor7, attenuation7, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight8, light8, lightColor8, attenuation8, v_position.xy, color, normal, v_color, useShadow) +
            calculateLight(isLight9, light9, lightColor9, attenuation9, v_position.xy, color, normal, v_color, useShadow);
        gl_FragColor = vec4(additiveBlending, color.a);
    } else {
        gl_FragColor = color;
    }
    if (drawNormals){
        gl_FragColor = vec4(normal, color.a);
    }
}

vec3 calculateLight(
                    bool isLight,
                    vec3 light,
                    vec3 lightColor,
                    float attenuation,
                    vec2 position,
                    vec4 textureColor,
                    vec3 normal,
                    vec4 vertexColor,
                    bool useShadow
                    ) {
    if (isLight){
        vec3 deltaPos = vec3( (light.xy - position), light.z );

        vec3 lightDir = normalize(deltaPos);
        float lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

        float d = sqrt(dot(deltaPos, deltaPos));
        float att = useShadow ? 1.0 / ( attenuation + (attenuation*d) + (attenuation*d*d) ) : 1.0;

        vec3 result = (lightColor.rgb * lambert) * att;
        result *= textureColor.rgb;
        result *= vertexColor.rgb;
        return result;
    } else {
        return vec3(0, 0, 0);
    }
}