local particles = CreateParticlePrototype("healinc", "/textures/particles/heal.png", 1)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0006)
particles:setStartSize(0.0)
particles:setEndSize(0.400)
particles:setMinimumLifetime(1500)
particles:setMaximumLifetime(1500)
particles:setStartColor(0.1, 0.99, 0.1, 1.0)
particles:setEndColor(0, 0.6, 0.3, 0.0)
particles:setMaximumAngle(1)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)

return particles