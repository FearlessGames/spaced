local particles = CreateParticlePrototype( "jetflame_gas", "/textures/particles/dot.png", 16)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.01211)
particles:setStartSize(0.272)
particles:setEndSize(1.29)
particles:setMinimumLifetime(60)
particles:setMaximumLifetime(520)

particles:setStartSpin(-0.2)
particles:setEndSpin(0.2)

particles:setStartColor(0.79, 0.4, 0.8, 0.43)
particles:setEndColor(0.99, 0.55, 0.3, 0.32)
particles:setMaximumAngle(11)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One") -- Thick Smoke, Alpha=Black, Black=Transparent
particles:setRepeatType( "CYCLE" ) -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene/followCharacterOrientation (true/false)
particles:addVortexInfluence(1, 0.10314, -0.1031, 0.0231, 0.011, false, true)

return particles

-- particles:setBlendState( true , "Zero" , "OneMinusSourceColor" ) -- Darkness Blend (black = invis, grey = black)
-- particles:setBlendState( true , "OneMinusDestinationColor" , "OneMinusSourceColor" ) -- Black=Invis, White=Foggy, Transparent = Bright 
-- particles:setBlendState( true , "DestinationColor" , "One" ) -- Weak Bright from white, black?
-- particles:setBlendState( true , "OneMinusDestinationColor" , "One" ) -- Strong Bright from white, black?
-- particles:setBlendState( true , "SourceAlpha" , "One" ) -- Add Blend
-- particles:setBlendState( true , "One" , "One" ) -- Strong Bright, Black = Invisible, Transparency = White
-- particles:setBlendState( true, "SourceAlpha" , "SourceColor" ) -- Strong bright (black = invisible)

-- particles:setBlendState( true , "OneMinusDestinationColor" , "OneMinusDestinationAlpha" ) -- = Crazy, useless?

-- particles:setBlendState( true , "DestinationColor" , "OneMinusSourceColor" ) = Invisible
-- particles:setBlendState( true , "Zero" , "One" ) = Invisible
-- particles:setBlendState( true , "OneMinusDestinationColor" , "SourceColor" ) = Crash
-- particles:setBlendState( true , "OneMinusSourceColor" , "SourceColor" ) = Crash

-- particles:setBlendState( true , "Source" , "Destination" ) -- syntax
--[[ http://ardor3d.com/release/0.4.1/javadoc/ardor3d-core/com/ardor3d/renderer/state/BlendState.SourceFunction.html

http://ardor3d.com/release/0.4.1/javadoc/ardor3d-core/com/ardor3d/renderer/state/BlendState.DestinationFunction.html 

]]