local particles = CreateParticlePrototype("lazor_blast_core1", "/textures/particles/dot.png", 195)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.00212)
particles:setStartSize(0.2)
particles:setEndSize(2.8)
particles:setStartSpin(0.0)
particles:setEndSpin(0.0)

particles:setMinimumLifetime(0910)
particles:setMaximumLifetime(1010)
particles:setStartColor(1, 0.4, 0.1, 0.081)
particles:setEndColor(1, 0.3, 0.2, 0)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
