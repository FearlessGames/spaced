local effect = CreateSoundPrototype("/sound/sfx/gear/jetpack/jetgas_start.wav.wav")

effect:setFalloff(1.0)
effect:setGain(0.48 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect