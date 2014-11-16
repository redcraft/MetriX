module.exports = function(grunt) {

	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		uglify: {
			options: {
				banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
			},
			build: {
				src: ['src/main/webapp/libs/*.js', 'src/main/webapp/app.js', 'src/main/webapp/components/*.js'],
				dest: 'src/main/webapp/min/app.min.js'
			}
		}
	});

	grunt.loadNpmTasks('grunt-contrib-uglify');

	grunt.registerTask('default', ['uglify']);
	grunt.registerTask('heroku', ['uglify']);

};