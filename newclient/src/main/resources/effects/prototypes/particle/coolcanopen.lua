local particles = CreateParticlePrototype("coolcanopen", "/textures/particles/dot.png", 8)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0004)
particles:setStartSize(0.3)
particles:setEndSize(0.0)
particles:setMinimumLifetime(1100)
particles:setMaximumLifetime(3000)
particles:setStartColor(0.0, 0.51, 0.7, 0.0)
particles:setEndColor(0.0, 0.02, 0.4, 0.9)
particles:setMaximumAngle(50)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
