local particles = CreateParticlePrototype("overcharge-hit", "/textures/particles/sinespin.png", 18)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.002191)
particles:setStartSize(0.06)
particles:setEndSize(0.012)

particles:setStartSpin(10.0)
particles:setEndSpin(15.0)

particles:setMinimumLifetime(100)
particles:setMaximumLifetime(260)
particles:setStartColor(0, 0.2, 1.0, 1)
particles:setEndColor(1, 0.6, 0.2, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
