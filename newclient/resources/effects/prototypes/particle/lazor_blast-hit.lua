local particles = CreateParticlePrototype("lazor_blast-hit", "/textures/particles/sinespin.png", 21)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0021)
particles:setStartSize(0.2)
particles:setEndSize(0.0)

particles:setStartSpin(0.0)
particles:setEndSpin(1.0)

particles:setMinimumLifetime(200)
particles:setMaximumLifetime(560)
particles:setStartColor(1, 0.5, 0.31, 1)
particles:setEndColor(1, 0.2, 0.3, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
