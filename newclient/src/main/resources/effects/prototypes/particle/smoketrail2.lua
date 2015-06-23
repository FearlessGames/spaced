local particles = CreateParticlePrototype( "smoketrail2", "/textures/particles/smoke.png", 8)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.002)
particles:setStartSize(0.0)
particles:setEndSize(4.6)
particles:setMinimumLifetime(900)
particles:setMaximumLifetime(1320)

particles:setStartSpin(-2.0)
particles:setEndSpin(-4.0)

particles:setStartColor(0.5, 0.3, 0.2, 0.5)
particles:setEndColor(0.0, 0.0, 0.0, 0.0)
particles:setMaximumAngle(350)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "OneMinusSourceAlpha") -- Thick Smoke, Alpha=Black, Black=Transparent
particles:setRepeatType( "WRAP" ) -- CLAMP/CYCLE/WRAP

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