local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/spin.png", 18)

particles:setEmissionDirection(0, 0, -1)
particles:setInitialVelocity(0.00011)
particles:setStartSize(0.37)

particles:setStartSpin(-2.0)
particles:setEndSpin(5.1)

particles:setEndSize(0.42)
particles:setMinimumLifetime(600)
particles:setMaximumLifetime(1200)
particles:setStartColor(0, 0.0, 0.8, 1)
particles:setEndColor(1, 0.3, 0.0, 0.0)
particles:setMaximumAngle(10)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)


return particles
