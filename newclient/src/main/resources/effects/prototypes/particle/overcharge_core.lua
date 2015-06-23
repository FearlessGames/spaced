local particles = CreateParticlePrototype("overcharge_core", "/textures/particles/energybullet.png", 15)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0029)
particles:setStartSize(1.2)
particles:setEndSize(0.5)
particles:setMinimumLifetime(150)
particles:setMaximumLifetime(150)
particles:setStartColor(0, 0.0, 1.0, 1)
particles:setEndColor(1, 0.6, 0.0, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
