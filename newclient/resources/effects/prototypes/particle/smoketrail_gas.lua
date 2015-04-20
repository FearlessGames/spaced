local particles = CreateParticlePrototype( "smoketrail_gas", "/textures/particles/smoke.png", 12)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.00499120)
particles:setStartSize(0.1)
particles:setEndSize(5.95)
particles:setMinimumLifetime(300)
particles:setMaximumLifetime(5000)
particles:setEmitter(CreatePointEmitter())

particles:setStartSpin(-0.2)
particles:setEndSpin(0.2)

particles:setStartColor(0.01611, 0.0133, 0.011, 0.420)
particles:setEndColor(0.631, 0.52, 0.3, 0.0)
particles:setMaximumAngle(51)
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