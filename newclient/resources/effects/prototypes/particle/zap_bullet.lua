local particles = CreateParticlePrototype("zap_bullet", "/textures/particles/energybullet.png", 15)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.000)
particles:setStartSize(0.0)
particles:setEndSize(0.1)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(300)
particles:setStartColor(1, 0.7, 0.4, 0.8)
particles:setEndColor(1, 0.7, 0.4, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
