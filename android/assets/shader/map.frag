#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

#define PI 3.1415926535897932384626433832795

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 position;
uniform sampler2D u_texture;

uniform float time;
uniform vec2 shipV;

uniform sampler2D foliage;
uniform sampler2D map;
uniform float atmosphere;
uniform float shipGlow;
uniform float thrustGlow;

void main() {
  vec4 map = texture2D(map, v_texCoords);
  vec2 normal = map.gb * 2.0 - 1.0;
  float power = map.r;

  vec2 v = v_texCoords.xy;
  vec2 p = -1.0 + 2.0 * v;
  float angle = atan(normal.y, normal.x);
  vec2 uv = v + cos(angle*43.3-time*2.1)*0.008*clamp(1.5-power*1.5, 0.0, 1.0);

  vec2 toShip = (shipV - position);

  float brightness = mix(pow(max(0.0, 1.0 - length(toShip)/500.0), 4.0) * 1.4, 1.0, atmosphere*0.8);
  float shortBrightness = pow(max(0.0, 1.0 - length(toShip)/100.0), 2.0);
  float specular = 1.0 + dot(normal, normalize(toShip)) * 0.3 * max(0.0, 2.0 - power*1.5);

  vec4 ground = texture2D(u_texture, v_texCoords);
  vec4 plants = texture2D(foliage, uv);

  vec3 color = (ground.rgb * (1.0-plants.a) + (plants.rgb * plants.a));
  color += vec3(0.0, 1.0, 0.5) * thrustGlow * shortBrightness * specular * 0.4;

  gl_FragColor = vec4(color * max(max(0.25, atmosphere), brightness*shipGlow) * specular, max(ground.a, plants.a));
}