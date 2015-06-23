local particles = CreateParticlePrototype("sparks", "/textures/particles/dot.png", 12)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0014)
particles:setStartSize(0.0)
particles:setEndSize(5.8)
particles:setMinimumLifetime(1930)
particles:setMaximumLifetime(3900)
particles:setStartColor(0.9, 0.7, 0.4, 0.5)
particles:setEndColor(1, 0.6, 0.3, 0)
particles:setMaximumAngle(70)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType( "CLAMP" ) -- CLAMP/CYCLE/WRAP

return particles