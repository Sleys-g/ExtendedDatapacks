package sleys.efedp.system.animations.json.properties.functional.helpers;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ParticleShapeHelper {

    public static List<Vec3> circle(double radius, int points) {
        List<Vec3> result = new ArrayList<>(points);
        double step = (Math.PI * 2) / points;
        for (int i = 0; i < points; i++) {
            double angle = step * i;
            result.add(new Vec3(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        }
        return result;
    }

    public static List<Vec3> arc(double radius, double startDeg, double endDeg, int points) {
        List<Vec3> result = new ArrayList<>(points);
        double start = Math.toRadians(startDeg);
        double end = Math.toRadians(endDeg);
        double step = (end - start) / Math.max(1, points - 1);
        for (int i = 0; i < points; i++) {
            double angle = start + step * i;
            result.add(new Vec3(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        }
        return result;
    }

    public static List<Vec3> sphereFibonacci(double radius, int points) {
        List<Vec3> result = new ArrayList<>(points);
        double goldenAngle = Math.PI * (3 - Math.sqrt(5)); // ~137.5°

        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) (points - 1)) * 2;
            double radiusAtY = Math.sqrt(1 - y * y);
            double theta = goldenAngle * i;

            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            result.add(new Vec3(x * radius, y * radius, z * radius));
        }
        return result;
    }

    public static List<Vec3> hemisphereFibonacci(double radius, int points) {
        return sphereFibonacci(radius, points * 2).stream()
                .filter(v -> v.y >= 0)
                .toList();
    }

    public static List<Vec3> cone(double radiusBottom, double radiusTop, double height,
                                  int rings, int pointsPerRing) {
        List<Vec3> result = new ArrayList<>();
        for (int r = 0; r <= rings; r++) {
            double t = r / (double) rings;
            double y = t * height;
            double radius = radiusBottom + (radiusTop - radiusBottom) * t;

            double step = (Math.PI * 2) / pointsPerRing;
            for (int i = 0; i < pointsPerRing; i++) {
                double angle = step * i;
                result.add(new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius));
            }
        }
        return result;
    }

    public static List<Vec3> helix(double radius, double height, double turns, int points) {
        List<Vec3> result = new ArrayList<>(points);
        double totalAngle = Math.PI * 2 * turns;
        double step = totalAngle / points;
        double heightStep = height / points;

        for (int i = 0; i < points; i++) {
            double angle = step * i;
            double y = heightStep * i;
            result.add(new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius));
        }
        return result;
    }

    public static List<Vec3> torus(double majorRadius, double minorRadius,
                                   int majorSegments, int minorSegments) {
        List<Vec3> result = new ArrayList<>();
        double majorStep = (Math.PI * 2) / majorSegments;
        double minorStep = (Math.PI * 2) / minorSegments;

        for (int i = 0; i < majorSegments; i++) {
            double u = majorStep * i;
            for (int j = 0; j < minorSegments; j++) {
                double v = minorStep * j;
                double x = (majorRadius + minorRadius * Math.cos(v)) * Math.cos(u);
                double y = minorRadius * Math.sin(v);
                double z = (majorRadius + minorRadius * Math.cos(v)) * Math.sin(u);
                result.add(new Vec3(x, y, z));
            }
        }
        return result;
    }

    public static List<Vec3> line(Vec3 from, Vec3 to, int points) {
        List<Vec3> result = new ArrayList<>(points);
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= points; i++) {
            double t = i / (double) points;
            result.add(from.add(delta.scale(t)));
        }
        return result;
    }

    public static List<Vec3> explosionDirections(int points) {
        return sphereFibonacci(1.0, points);
    }

    public static List<Vec3> alignToDirection(List<Vec3> points, Vec3 direction) {
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 dir = direction.normalize();

        if (dir.distanceToSqr(up) < 1.0E-6) return points;

        Vec3 axis = up.cross(dir).normalize();
        double angle = Math.acos(Mth.clamp(up.dot(dir), -1.0, 1.0));

        List<Vec3> result = new ArrayList<>(points.size());
        for (Vec3 p : points) {
            result.add(rotateAroundAxis(p, axis, angle));
        }
        return result;
    }

    private static Vec3 rotateAroundAxis(Vec3 point, Vec3 axis, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);

        Vec3 term1 = point.scale(cos);
        Vec3 term2 = axis.cross(point).scale(sin);
        Vec3 term3 = axis.scale(axis.dot(point) * (1 - cos));

        return term1.add(term2).add(term3);
    }

    public static Vec3 randomInSphere(double radius, RandomSource random) {
        double theta = random.nextDouble() * Math.PI * 2;
        double phi = Math.acos(2 * random.nextDouble() - 1);
        double r = radius * Math.cbrt(random.nextDouble());
        return new Vec3(
                r * Math.sin(phi) * Math.cos(theta),
                r * Math.sin(phi) * Math.sin(theta),
                r * Math.cos(phi)
        );
    }

    public static List<Vec3> diskFill(double radius, int points, RandomSource random) {
        List<Vec3> result = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double r = radius * Math.sqrt(random.nextDouble());
            double angle = random.nextDouble() * Math.PI * 2;
            result.add(new Vec3(Math.cos(angle) * r, 0, Math.sin(angle) * r));
        }
        return result;
    }

    public static List<Vec3> boxOutline(double sx, double sy, double sz, int pointsPerEdge) {
        List<Vec3> result = new ArrayList<>();
        double hx = sx / 2, hy = sy / 2, hz = sz / 2;
        Vec3[] corners = {
                new Vec3(-hx,-hy,-hz), new Vec3(hx,-hy,-hz), new Vec3(hx,-hy,hz), new Vec3(-hx,-hy,hz),
                new Vec3(-hx,hy,-hz),  new Vec3(hx,hy,-hz),  new Vec3(hx,hy,hz),  new Vec3(-hx,hy,hz)
        };
        int[][] edges = {{0,1},{1,2},{2,3},{3,0}, {4,5},{5,6},{6,7},{7,4}, {0,4},{1,5},{2,6},{3,7}};
        for (int[] e : edges) result.addAll(line(corners[e[0]], corners[e[1]], pointsPerEdge));
        return result;
    }

    public static List<Vec3> boxFill(double sx, double sy, double sz, int points, RandomSource random) {
        List<Vec3> result = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            result.add(new Vec3(
                    (random.nextDouble() - 0.5) * sx,
                    (random.nextDouble() - 0.5) * sy,
                    (random.nextDouble() - 0.5) * sz
            ));
        }
        return result;
    }

    public static List<Vec3> coneDirections(Vec3 axis, double angleDeg, int points, RandomSource random) {
        List<Vec3> result = new ArrayList<>(points);
        double maxAngleRad = Math.toRadians(angleDeg);
        double cosMax = Math.cos(maxAngleRad);
        for (int i = 0; i < points; i++) {
            double z = 1 - random.nextDouble() * (1 - cosMax);
            double phi = random.nextDouble() * Math.PI * 2;
            double s = Math.sqrt(1 - z * z);
            Vec3 local = new Vec3(s * Math.cos(phi), s * Math.sin(phi), z);
            result.add(alignSingleToDirection(local, axis));
        }
        return result;
    }

    public static List<Vec3> rectangleDirections(Vec3 axis, double widthDeg, double heightDeg,
                                                 int points, RandomSource random) {
        List<Vec3> result = new ArrayList<>(points);
        double maxYaw = Math.toRadians(widthDeg / 2);
        double maxPitch = Math.toRadians(heightDeg / 2);
        for (int i = 0; i < points; i++) {
            double yaw = (random.nextDouble() * 2 - 1) * maxYaw;
            double pitch = (random.nextDouble() * 2 - 1) * maxPitch;
            Vec3 local = new Vec3(Math.sin(yaw), Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
            result.add(alignSingleToDirection(local, axis));
        }
        return result;
    }

    public static Vec3 alignSingleToDirection(Vec3 point, Vec3 direction) {
        Vec3 up = new Vec3(0, 0, 1);
        Vec3 dir = direction.normalize();
        if (dir.distanceToSqr(up) < 1.0E-6) return point;
        Vec3 axis = up.cross(dir).normalize();
        double angle = Math.acos(Mth.clamp(up.dot(dir), -1.0, 1.0));
        return rotateAroundAxis(point, axis, angle);
    }

    public static void spawnAtPoints(Level level, ParticleOptions particle, Vec3 origin,
                                     List<Vec3> localOffsets, double speed, boolean velocityOutward) {
        for (Vec3 offset : localOffsets) {
            Vec3 world = origin.add(offset);
            Vec3 velocity = (velocityOutward && offset.lengthSqr() > 1.0E-6)
                    ? offset.normalize().scale(speed)
                    : Vec3.ZERO;
            level.addParticle(particle, world.x, world.y, world.z, velocity.x, velocity.y, velocity.z);
        }
    }
}
