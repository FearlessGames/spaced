local particles = CreateParticlePrototype( "charging", "/textures/particles/dot.png", 15 )

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.00092)
particles:setStartSize(0.0)
particles:setEndSize(1.0)
particles:setMinimumLifetime(100)
particles:setMaximumLifetime(1500)
particles:setStartColor(0.4, 0.6, 0.8, 0.0)
particles:setEndColor(0, 0.0, 0, 1)
particles:setMaximumAngle(70)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "OneMinusDestinationColor" , "One" )
particles:setRepeatType( "WRAP" ) -- CLAMP/CYCLE/WRAP

return particles
