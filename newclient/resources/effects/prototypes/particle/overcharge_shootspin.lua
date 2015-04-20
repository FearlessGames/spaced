local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/spin.png", 48)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.00031)
particles:setStartSize(0.21)

particles:setStartSpin(-8.0)
particles:setEndSpin(18.1)

particles:setEndSize(0.150)
particles:setMinimumLifetime(400)
particles:setMaximumLifetime(700)
particles:setStartColor(0, 0.0, 0.8, 1)
particles:setEndColor(1, 0.3, 0.0, 0.0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
