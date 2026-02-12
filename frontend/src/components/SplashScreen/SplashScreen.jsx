import React, { useState, useEffect } from 'react';
import { motion, useMotionValue, useSpring, useTransform } from 'framer-motion';

const numberOfCircles = 4;
const radius = 40;

const SplashScreen = ({ onComplete }) => {
  const [isVisible, setIsVisible] = useState(true);
  const progress = useMotionValue(0);
  const springProgress = useSpring(progress, { stiffness: 100, damping: 10 });

  const points = Array.from({ length: numberOfCircles }, (_, i) => {
    const angle = (2 * Math.PI * i) / numberOfCircles;
    return {
      x: radius * Math.cos(angle),
      y: radius * Math.sin(angle),
    };
  });

  useEffect(() => {
    let animationFrameId;
    let startTime;

    const animate = (time) => {
      if (!startTime) startTime = time;
      const elapsed = time - startTime;

      const loopDuration = 2000;
      const progressValue = (elapsed % loopDuration) / loopDuration;
      progress.set(progressValue);

      animationFrameId = requestAnimationFrame(animate);
    };

    animationFrameId = requestAnimationFrame(animate);

    return () => cancelAnimationFrame(animationFrameId);
  }, [progress]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      if (onComplete) {
        onComplete();
      }
    }, 700);

    return () => clearTimeout(timer);
  }, [onComplete]);

  return (
    <motion.div
      className={`fixed top-0 left-0 w-full h-full flex justify-center items-center z-[1000] ${!isVisible ? 'pointer-events-none' : ''}`}
      initial={{ opacity: 1 }}
      animate={{ opacity: isVisible ? 1 : 0 }}
      transition={{ duration: 0.5, ease: 'easeInOut' }}
    >
      <motion.div
        className="absolute top-0 left-0 w-full h-full bg-black bg-opacity-50"
        initial={{ opacity: 0 }}
        animate={{ opacity: isVisible ? 0.5 : 0 }}
        transition={{ duration: 0.5, ease: 'easeInOut' }}
      />
      <div className="relative w-[80px] h-[80px] rounded-full">
        {points.map((point, index) => {
          const rotate = useTransform(springProgress, [0, 1], [0, 2 * Math.PI]);

          const x = useTransform(
            rotate,
            (pos) => point.x * Math.cos(pos) - point.y * Math.sin(pos)
          );

          const y = useTransform(
            rotate,
            (pos) => point.x * Math.sin(pos) + point.y * Math.cos(pos)
          );

          return (
            <motion.div
              key={index}
              className={`absolute w-[16px] h-[16px] rounded-full ${
                index === 0
                  ? 'bg-[#ff6b6b]'
                  : index === 1
                    ? 'bg-[#ffd93d]'
                    : index === 2
                      ? 'bg-[#6bcb77]'
                      : 'bg-[#4d96ff]'
              }`}
              style={{
                translateX: x,
                translateY: y,
              }}
              animate={{ scale: [0.8, 1.2, 0.8] }}
              transition={{
                loop: Infinity,
                duration: 0.8,
                ease: 'easeInOut',
              }}
            />
          );
        })}
      </div>
    </motion.div>
  );
};

export default SplashScreen;
