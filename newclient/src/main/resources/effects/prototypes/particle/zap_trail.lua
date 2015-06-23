local particles = CreateParticlePrototype("zap_trail", "/textures/particles/arc.png", 35)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.0052)
particles:setStartSize(1.5)
particles:setEndSize(0.1)
particles:setStartSpin(0.0)
particles:setEndSpin(0.0)

particles:setMinimumLifetime(180)
particles:setMaximumLifetime(190)
particles:setStartColor(1, 0.7, 0.8, 0.04)
particles:setEndColor(1, 0.7, 0.8, 0)
particles:setMaximumAngle(90)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
