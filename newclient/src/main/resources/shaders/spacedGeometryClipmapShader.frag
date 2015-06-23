/**
 * Copyright (c) 2008-2010 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

uniform sampler3D texture;
uniform float levels;
uniform vec2 sliceOffset[16];
uniform float minLevel;
uniform float validLevels;
uniform float textureSize;
uniform float texelSize;
uniform float showDebug;
uniform vec3 eyePosition;

varying vec2 vVertex;
varying vec2 texCoord;
varying vec3 eyeSpacePosition;

uniform vec4 diffuse,ambient;
uniform vec3 lightDir;
uniform vec3 normal;

vec4 texture3DBilinear( const in sampler3D textureSampler, const in vec3 uv, const in vec2 offset )
{
    vec4 tl = texture3D(textureSampler, uv);
    vec4 tr = texture3D(textureSampler, uv + vec3(texelSize, 0, 0));
    vec4 bl = texture3D(textureSampler, uv + vec3(0, texelSize, 0));
    vec4 br = texture3D(textureSampler, uv + vec3(texelSize , texelSize, 0));

    vec2 f = fract( uv.xy * textureSize + 0.25 * texelSize );
    //vec2 f = fract( uv.xy * textureSize ); // +texelSize on ATI?

    vec4 tA = mix( tl, tr, f.x );
    vec4 tB = mix( bl, br, f.x );

    return mix( tA, tB, f.y );
}

vec2 slice(const in int x)
{
	if(x == 0) return sliceOffset[0];
	if(x == 1) return sliceOffset[1];
	if(x == 2) return sliceOffset[2];
	if(x == 3) return sliceOffset[3];
	if(x == 4) return sliceOffset[4];
	if(x == 5) return sliceOffset[5];
	if(x == 6) return sliceOffset[6];
	if(x == 7) return sliceOffset[7];
	if(x == 8) return sliceOffset[8];
	if(x == 9) return sliceOffset[9];
	if(x == 10) return sliceOffset[10];
	if(x == 11) return sliceOffset[11];
	if(x == 12) return sliceOffset[12];
	if(x == 13) return sliceOffset[13];
	if(x == 14) return sliceOffset[14];
	return sliceOffset[15];
}

void main()
{
	float unit = (max(abs(vVertex.x), abs(vVertex.y)));

	unit = floor(unit);
	unit = log2(unit);
	unit = floor(unit);

	unit = min(unit, validLevels);
    unit = max(unit, minLevel);

	vec2 offset = slice(int(unit));
	float frac = unit;
	frac = exp2(frac);
	frac *= 4.0; //Magic number
	vec2 texCoord = vVertex/vec2(frac);
	vec2 fadeCoord = texCoord;
	texCoord += vec2(0.5);
	texCoord *= vec2(1.0 - texelSize);
	texCoord += offset;

	float unit2 = unit + 1.0;
	unit2 = min(unit2, validLevels);

	vec2 offset2 = slice(int(unit2));

	/*
		Created the ugly switch because of this failure:
		GLSL 1.4 allows indexing with constant values, but not for arbitrary ints.

		Error message when it was sliceOffset[variable int] instead:

			  [java] 17:33:56.185 [main] ERROR c.a.s.s.l.LwjglShaderObjectsStateUtil - Fragment info
			  [java] -------------
			  [java] 0(56) : error C6013: Only arrays of texcoords may be indexed in this profile, and only with a loop index variable
			  [java] 0(68) : error C6013: Only arrays of texcoords may be indexed in this profile, and only with a loop index variable
			  [java]
			  [java] Exception in thread "main" com.ardor3d.util.Ardor3dException: Error linking GLSL shader: Fragment info
			  [java] -------------
			  [java] 0(56) : error C6013: Only arrays of texcoords may be indexed in this profile, and only with a loop index variable
			  [java] 0(68) : error C6013: Only arrays of texcoords may be indexed in this profile, and only with a loop index variable
			  [java]
			  [java] 	at com.ardor3d.scene.state.lwjgl.LwjglShaderObjectsStateUtil.checkLinkError(LwjglShaderObjectsStateUtil.java:152)
			  [java] 	at com.ardor3d.scene.state.lwjgl.LwjglShaderObjectsStateUtil.sendToGL(LwjglShaderObjectsStateUtil.java:128)
			  [java] 	at com.ardor3d.scene.state.lwjgl.LwjglShaderObjectsStateUtil.apply(LwjglShaderObjectsStateUtil.java:225)
			  [java] 	at com.ardor3d.renderer.lwjgl.LwjglRenderer.doApplyState(LwjglRenderer.java:1558)
			  [java] 	at com.ardor3d.renderer.AbstractRenderer.applyState(AbstractRenderer.java:82)
			  [java] 	at com.ardor3d.scenegraph.Mesh.render(Mesh.java:253)
			  [java] 	at com.ardor3d.scenegraph.Mesh.render(Mesh.java:227)
			  [java] 	at com.ardor3d.renderer.lwjgl.LwjglRenderer.draw(LwjglRenderer.java:605)
			  [java] 	at com.ardor3d.scenegraph.Mesh.draw(Mesh.java:397)
			  [java] 	at com.ardor3d.renderer.queue.AbstractRenderBucket.render(AbstractRenderBucket.java:73)
			  [java] 	at com.ardor3d.renderer.queue.RenderQueue.renderBuckets(RenderQueue.java:92)
			  [java] 	at com.ardor3d.renderer.lwjgl.LwjglRenderer.renderBuckets(LwjglRenderer.java:138)
			  [java] 	at com.ardor3d.renderer.lwjgl.LwjglRenderer.renderBuckets(LwjglRenderer.java:131)
			  [java] 	at se.spaced.client.ardor.SpacedScene.renderUnto(SpacedScene.java:61)
			  [java] 	at com.ardor3d.framework.lwjgl.LwjglCanvasRenderer.draw(LwjglCanvasRenderer.java:133)
			  [java] 	at com.ardor3d.framework.lwjgl.LwjglCanvas.draw(LwjglCanvas.java:124)
			  [java] 	at com.ardor3d.framework.FrameHandler.updateFrame(FrameHandler.java:90)
			  [java] 	at se.ardortech.BaseArdorMain.update(BaseArdorMain.java:45)
			  [java] 	at se.spaced.client.ardor.Spaced.update(Spaced.java:46)
			  [java] 	at se.ardortech.BaseArdorMain.mainLoop(BaseArdorMain.java:38)
			  [java] 	at se.ardortech.BaseArdorMain.run(BaseArdorMain.java:32)
			  [java] 	at se.spaced.client.ardor.Spaced.run(Spaced.java:40)
			  [java] 	at se.spaced.client.launcher.ArdorRealGame.start(ArdorRealGame.java:63)
			  [java] 	at se.spaced.client.launcher.ArdorRealGame.main(ArdorRealGame.java:32)

	 */
	float frac2 = unit2;
	frac2 = exp2(frac2);
	frac2 *= 4.0; //Magic number
	vec2 texCoord2 = vVertex/vec2(frac2);
	texCoord2 += vec2(0.5);
	texCoord2 *= vec2(1.0 - texelSize);
	texCoord2 += offset2;

	unit /= levels;
	unit = clamp(unit, 0.0, 0.99);

	unit2 /= levels;
	unit2 = clamp(unit2, 0.0, 0.99);

//	vec4 tex = texture3D(texture, vec3(texCoord.x, texCoord.y, unit));
//	vec4 tex2 = texture3D(texture, vec3(texCoord2.x, texCoord2.y, unit2));
	vec4 tex = texture3DBilinear(texture, vec3(texCoord.x, texCoord.y, unit), offset);
	vec4 tex2 = texture3DBilinear(texture, vec3(texCoord2.x, texCoord2.y, unit2), offset2);

	float fadeVal1 = abs(fadeCoord.x)*2.05;
	float fadeVal2 = abs(fadeCoord.y)*2.05;
	float fadeVal = max(fadeVal1, fadeVal2);
	fadeVal = max(0.0, fadeVal-0.8)*5.0;
	fadeVal = min(1.0, fadeVal);
    vec4 texCol = mix(tex, tex2, fadeVal) + vec4(fadeVal*showDebug);

	vec4 vDiffuse = diffuse * vec4(max( dot(lightDir, normalize(normal)), 0.0 ));
	texCol = (ambient + vDiffuse) * texCol;

    float dist = length(eyeSpacePosition);
	float fog = clamp((gl_Fog.end - dist) * gl_Fog.scale, 0.0, 1.0);
    gl_FragColor = mix(gl_Fog.color, texCol, fog);
}
