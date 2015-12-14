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

uniform vec2 shipV;

uniform sampler2D map;
uniform float atmosphere;
uniform float shipGlow;
uniform float thrustGlow;

void main() {
  vec4 map = texture2D(map, v_texCoords);
  float power = map.r;

  vec2 toShip = (shipV - position);

  float brightness = mix(pow(max(0.0, 1.0 - length(toShip)/500.0), 4.0) * 1.4, 1.0, atmosphere*0.8);
  float shortBrightness = pow(max(0.0, 1.0 - length(toShip)/100.0), 2.0);

  float bubbleDistance = max(0.0, 1.0-power);
  float bubble = 0.4*cos(clamp(bubbleDistance-0.21, -0.2, 0.2)*PI/0.2)+0.4;

  vec3 color = vec3(0.2, 0.3, 0.5);
  color += vec3(0.0, 1.0, 0.5) * thrustGlow * shortBrightness * 0.25;

  gl_FragColor = vec4(color * max(0.25, brightness*shipGlow), atmosphere*bubble);
}