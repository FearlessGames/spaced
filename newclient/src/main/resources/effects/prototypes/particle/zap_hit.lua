local particles = CreateParticlePrototype("zap_hit", "/textures/particles/shield.png", 1)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.00001)
particles:setStartSize(1.52)
particles:setEndSize(0.0)

particles:setStartSpin(0.0)
particles:setEndSpin(0.2)

particles:setMinimumLifetime(200)
particles:setMaximumLifetime(260)
particles:setStartColor(1, 0.7, 0.4, 0.0)
particles:setEndColor(1, 0.7, 0.4, 0.152)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
