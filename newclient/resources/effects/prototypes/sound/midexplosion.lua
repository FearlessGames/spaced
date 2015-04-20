local effect = CreateSoundPrototype("/sound/sfx//events/explosions/midexplosion.wav")

effect:setFalloff(1.1)
effect:setGain(1.7 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect