local particles = CreateParticlePrototype("zap_charge", "/textures/particles/arc.png", 12)

particles:setEmissionDirection(0, -1, 0)
particles:setInitialVelocity(0.000025)
particles:setStartSize(0.0)
particles:setEndSize(0.4)
particles:setMinimumLifetime(400)
particles:setMaximumLifetime(930)
particles:setStartColor(1, 0.8, 0.8, 0.21)
particles:setEndColor(0.7, 0.8, 0.8, 0)
particles:setMaximumAngle(222)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
