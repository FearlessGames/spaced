local particles = CreateParticlePrototype("lazor_blast-shock", "/textures/particles/dot.png", 31)

particles:setEmissionDirection(0, -1, 0)
particles:setInitialVelocity(0.0001)
particles:setStartSize(0.0)
particles:setEndSize(0.3)
particles:setMinimumLifetime(120)
particles:setMaximumLifetime(230)
particles:setEmitter(CreateRingEmitter(0, 0, 0, 0, 0.5, 0, 0.5, 0.5))
particles:setStartColor(0.1, 0.5, 0.9, 0.3)
particles:setEndColor(0.2, 0.6, 0.9, 0.0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CYCLE") -- CLAMP/CYCLE/WRAP

return particles