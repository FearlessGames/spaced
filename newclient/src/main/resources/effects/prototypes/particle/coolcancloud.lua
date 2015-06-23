local particles = CreateParticlePrototype("coolcancloud", "/textures/particles/smoke.png", 10)

particles:setEmissionDirection(0, 1, -0.6)
particles:setInitialVelocity(-0.0006)
particles:setStartSize(0.11)
particles:setEndSize(0.700)
particles:setMinimumLifetime(1100)
particles:setMaximumLifetime(2800)
particles:setStartColor(0.0, 0.09, 0.2, 0.79)
particles:setEndColor(0.1, 0.29, 0.4, 0.0)
particles:setMaximumAngle(30)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)

return particles