local effect = CreateSoundPrototype("/sound/sfx/Plasmaball/PlasmaHit1.wav")

effect:setFalloff(1.2)
effect:setGain(1.0 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect