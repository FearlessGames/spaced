local particles = CreateParticlePrototype("lazor_blast-squash", "/textures/particles/shockwave.png", 2)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.00051)
particles:setStartSize(2.0)
particles:setEndSize(0.0)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(220)
particles:setStartColor(1, 0.0, 0.4, 0.0)
particles:setEndColor(1, 0.2, 0.4, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
