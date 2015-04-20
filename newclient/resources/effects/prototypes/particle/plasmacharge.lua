local particles = CreateParticlePrototype("plasmacharge", "/textures/particles/dot.png", 9)

--particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0008)
particles:setStartSize(0.4)
particles:setEndSize(0.0)
particles:setMinimumLifetime(400)
particles:setMaximumLifetime(500)
particles:setStartColor(1.0, 0.3, 0.1, 0)
particles:setEndColor(0.9, 0.3, 0.2, 1)
particles:setMaximumAngle(120)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
