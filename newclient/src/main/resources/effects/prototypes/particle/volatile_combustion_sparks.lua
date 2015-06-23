local particles = CreateParticlePrototype("overcharge_sparks", "/textures/particles/sinespin.png", 11)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.00030)
particles:setStartSize(0.2)
particles:setEndSize(0.0)

particles:setStartSpin(-10.0)
particles:setEndSpin(20.0)

particles:setMinimumLifetime(900)
particles:setMaximumLifetime(1200)
particles:setStartColor(1, 0.0, 0.2, 1)
particles:setEndColor(0, 1.0, 0.0, 0.6)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
