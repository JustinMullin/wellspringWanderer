#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 position;
uniform sampler2D u_texture;

uniform vec2 center;
uniform float time;

void main() {
  float power = max(0.0, 1.0 - length(position - center) / (16.0 * min(1.0, time*10.0)));
  float halo = max(0.0, 1.0 - length(position - center) / (64.0 * min(1.0, time*3.0)));

  gl_FragColor =
    vec4(time*0.2, 0.8, 0.2, min(1.0, power*2.0) * (1.0-time)) +
    vec4(0.4-time*0.2, 1.0, 0.5-time*0.1, min(1.0, pow(halo, 2.0)) * (1.0-time));
}