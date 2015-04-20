local particles = CreateParticlePrototype("zap_splash", "/textures/particles/dot.png", 8)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0021)
particles:setStartSize(0.31)
particles:setEndSize(0.0)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(220)
particles:setStartColor(1, 0.7, 0.4, 0.0)
particles:setEndColor(1, 0.7, 0.4, 0.4)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
