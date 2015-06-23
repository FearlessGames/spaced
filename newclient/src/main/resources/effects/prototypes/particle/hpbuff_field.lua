local particles = CreateParticlePrototype("lazor_blast-shock", "/textures/particles/shockwave.png", 11)

particles:setEmissionDirection(0, -1, 0)
particles:setInitialVelocity(0.00041)
particles:setStartSize(0.5)
particles:setEndSize(0.55)
particles:setMinimumLifetime(90)
particles:setMaximumLifetime(240)
particles:setStartColor(0.1, 0.0, 0.0, 0.2)
particles:setEndColor(0.0, 0.5, 0.4, 0.0)
particles:setMaximumAngle(20)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles