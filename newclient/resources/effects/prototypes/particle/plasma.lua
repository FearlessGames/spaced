local particles = CreateParticlePrototype("plasma", "/textures/particles/fieldball.png", 45)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0041)
particles:setStartSize(0.5)
particles:setEndSize(0.1)
particles:setMinimumLifetime(50)
particles:setMaximumLifetime(120)
particles:setStartColor(0.2, 0.3, 1, 1)
particles:setEndColor(0.3, 0.5, 0.9, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
