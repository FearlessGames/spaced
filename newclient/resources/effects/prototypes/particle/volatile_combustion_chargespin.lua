local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/spin.png", 18)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.000021)
particles:setStartSize(0.24)

particles:setStartSpin(-2.0)
particles:setEndSpin(8.1)

particles:setEndSize(0.2)
particles:setMinimumLifetime(600)
particles:setMaximumLifetime(1200)
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
