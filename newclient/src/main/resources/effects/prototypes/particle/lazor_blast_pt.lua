local particles = CreateParticlePrototype("lazor_blast_pt", "/textures/particles/fail.png", 15)

--particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.001)
particles:setStartSize(0.1)
particles:setEndSize(2.3)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(5000)
particles:setStartColor(0.0, 0.3, 1, 1)
particles:setEndColor(0.2, 0.3, 0.4, 1)
particles:setMaximumAngle(120)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
