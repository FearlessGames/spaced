local particles = CreateParticlePrototype("lazor_blast-shock", "/textures/particles/shockwave.png", 1)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0001)
particles:setStartSize(0.2)
particles:setEndSize(1.5)
particles:setMinimumLifetime(400)
particles:setMaximumLifetime(420)
particles:setStartColor(1, 0.4, 0.6, 0.5)
particles:setEndColor(1, 0.2, 0.4, 0.0)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
