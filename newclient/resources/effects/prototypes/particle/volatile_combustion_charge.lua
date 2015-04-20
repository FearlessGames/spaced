local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/energybullet.png", 12)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.00021)
particles:setStartSize(0.4)
particles:setStartSpin(0.0)
particles:setEndSpin(0.1)
particles:setEndSize(0.0)
particles:setMinimumLifetime(1100)
particles:setMaximumLifetime(2800)
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
