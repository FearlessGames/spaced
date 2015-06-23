local effect = CreateSoundPrototype("/sound/sfx/gear/jetpack/jetgas_looptone.wav.wav")

effect:setFalloff(1.0)
effect:setGain(0.22 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect