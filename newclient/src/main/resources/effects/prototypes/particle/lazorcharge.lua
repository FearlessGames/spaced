local particles = CreateParticlePrototype("lazorcharge", "/textures/particles/energybullet.png", 25)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.000210)
particles:setStartSize(0.22)
particles:setEndSize(0.02)
particles:setMinimumLifetime(500)
particles:setMaximumLifetime(1700)
particles:setStartColor(1, 0.4, 0.4, 0.3)
particles:setEndColor(1, 0.0, 0.4, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
