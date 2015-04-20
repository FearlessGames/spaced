local particles = CreateParticlePrototype("zap_shock", "/textures/particles/arc.png", 12)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.002101)
particles:setStartSize(0.0)
particles:setEndSize(0.4)
particles:setMinimumLifetime(300)
particles:setMaximumLifetime(420)
particles:setStartColor(1, 0.7, 0.4, 0.64)
particles:setEndColor(1, 0.7, 0.4, 0)
particles:setMaximumAngle(260)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
