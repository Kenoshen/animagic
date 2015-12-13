attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_proj;
uniform mat4 u_trans;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;
varying vec2 v_norCoords;

void main()
{
   v_color = a_color;
   v_texCoords = a_texCoord0;
   v_position = u_projTrans * a_position;
   gl_Position =  v_position;
}