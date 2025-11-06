#!/usr/bin/env node

/**
 * Simple helper used by the frontend-maven-plugin to compile the evaluation
 * tool SASS bundle to CSS as part of the Maven build.
 */
const fs = require("fs");
const path = require("path");
const sass = require("sass");

const args = process.argv.slice(2);

const defaultOutputDir = path.resolve(__dirname, "../../../../target/generated-resources/css");
let outputDir = defaultOutputDir;
let sourceMap = false;

args.forEach((arg, index) => {
  if (arg.startsWith("--outputDir=")) {
    outputDir = path.resolve(process.cwd(), arg.split("=")[1]);
  } else if (arg === "--outputDir") {
    const next = args[index + 1];
    if (next) {
      outputDir = path.resolve(process.cwd(), next);
    }
  } else if (arg === "--sourceMap") {
    sourceMap = true;
  }
});

const ensureDir = (dir) => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
};

const compile = () => {
  const entryFile = path.resolve(__dirname, "../scss/evaluation_base.scss");
  const result = sass.compile(entryFile, {
    loadPaths: [path.resolve(__dirname, "../scss")],
    style: "expanded",
    sourceMap: sourceMap
  });

  ensureDir(outputDir);
  const cssOutputPath = path.join(outputDir, "evaluation_base.css");
  fs.writeFileSync(cssOutputPath, result.css, "utf8");

  if (sourceMap && result.sourceMap) {
    const mapPath = `${cssOutputPath}.map`;
    fs.writeFileSync(mapPath, JSON.stringify(result.sourceMap), "utf8");
  }
};

compile();
