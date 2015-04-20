local particles = CreateParticlePrototype("plasmacore", "/textures/particles/dot.png", 11)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0086)
particles:setStartSize(0.3)
particles:setEndSize(1.8)
particles:setMinimumLifetime(60)
particles:setMaximumLifetime(130)
particles:setStartColor(0.0, 0.4, 0.8, 1)
particles:setEndColor(1.0, 1.0, 0.0, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
