local particles = CreateParticlePrototype("lazor_blast", "/textures/particles/dot.png", 25)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0031)
particles:setStartSize(0.0)
particles:setEndSize(0.8)
particles:setMinimumLifetime(100)
particles:setMaximumLifetime(300)
particles:setStartColor(1, 0.5, 0.7, 0.31)
particles:setEndColor(1, 0.2, 0.1, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
