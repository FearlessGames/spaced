local effect = CreateSoundPrototype("/sound/sfx/gear/jetpack/jetbase_start.wav.wav")

effect:setFalloff(1.0)
effect:setGain(0.49 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect