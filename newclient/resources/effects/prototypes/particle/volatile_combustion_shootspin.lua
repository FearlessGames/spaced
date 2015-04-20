local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/spin.png", 48)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.000231)
particles:setStartSize(0.261)

particles:setStartSpin(-8.0)
particles:setEndSpin(18.1)

particles:setEndSize(0.50)
particles:setMinimumLifetime(400)
particles:setMaximumLifetime(1100)
particles:setStartColor(1, 0.0, 0.2, 1)
particles:setEndColor(0, 1.0, 0.0, 0.0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
