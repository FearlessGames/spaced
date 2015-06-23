local particles = CreateParticlePrototype("healhitfield", "/textures/particles/fieldring.png", 3)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0004)
particles:setStartSize(1.8)
particles:setEndSize(0.0)
particles:setMinimumLifetime(220)
particles:setMaximumLifetime(300)
particles:setStartColor(0.0, 1, 0.1, 0.0)
particles:setEndColor(1, 1.0, 1.0, 0.2)
particles:setMaximumAngle(60)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(2)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)
particles:addVortexInfluence(0, 1, 2.3, -0.003, 1, true, true)

return particles
