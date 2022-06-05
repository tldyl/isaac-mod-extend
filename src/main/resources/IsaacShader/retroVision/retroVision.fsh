uniform sampler2D u_texture;
uniform float granularity;
uniform vec2 u_resolution;

varying vec4 v_color; //sb.setColor(color)中的color
varying vec2 v_texCoord; //当前使用的纹理要取颜色值的位置(已做规范化处理)，和gl_FragCoord不同，gl_FragCoord是指当前屏幕上正在渲染的像素点位置

void main() {
    vec2 flooredTexCoord = vec2(v_texCoord);
    flooredTexCoord.x *= u_resolution.x;
    flooredTexCoord.y *= u_resolution.y;
    flooredTexCoord = floor(flooredTexCoord / granularity) * granularity;
    flooredTexCoord.x /= u_resolution.x;
    flooredTexCoord.y /= u_resolution.y;
    vec4 texColor = texture2D(u_texture, flooredTexCoord);
    gl_FragColor = v_color * texColor;
}
