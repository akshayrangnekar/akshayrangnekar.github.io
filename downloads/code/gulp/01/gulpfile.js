// Include gulp
var gulp = require('gulp');
var stylus = require('gulp-stylus');

gulp.task('styl', function() {
  return gulp.src('src/styl/main.styl')
    .pipe(stylus())
    .pipe(gulp.dest('app/css'))
});
