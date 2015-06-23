local particles = CreateParticlePrototype("lilheal_charge", "/textures/particles/shockwave.png", 6)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.000081)
particles:setStartSize(0.15)
particles:setEndSize(0.10)
particles:setMinimumLifetime(1900)
particles:setMaximumLifetime(2400)
particles:setStartColor(1, 0.8, 0.8, 0.21)
particles:setEndColor(0.7, 0.8, 0.8, 0)
particles:setMaximumAngle(200)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles

