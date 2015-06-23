local particles = CreateParticlePrototype( "shockwave", "/textures/particles/shockwave.png", 3 )

particles:setEmissionDirection(0, 0, 1)
particles:setInitialVelocity(0.00)
particles:setStartSize(0.0)
particles:setEndSize(5.8)
particles:setMinimumLifetime(800)
particles:setMaximumLifetime(800)
particles:setStartColor(0.9, 0.5, 0.3, 1)
particles:setEndColor(0, 0, 0, 1)
particles:setMaximumAngle(90)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType( "CLAMP" ) -- CLAMP/CYCLE/WRAP

return particles