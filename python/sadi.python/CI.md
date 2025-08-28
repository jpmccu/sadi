# Continuous Integration Documentation

This document describes the CI/CD setup for the SADI Python project.

## Overview

The SADI Python project uses GitHub Actions for continuous integration and automated testing. The CI system ensures code quality, compatibility across Python versions, and successful package building.

## CI Workflows

### Python CI Workflow (`.github/workflows/python-ci.yml`)

The main CI workflow runs on:
- **Push events** to `main`, `master`, or `develop` branches (when Python files change)
- **Pull requests** to `main`, `master`, or `develop` branches (when Python files change)
- **Manual dispatch** (workflow_dispatch)

#### Jobs

##### 1. Test Job (`test`)
- **Matrix Strategy**: Tests across Python versions 3.8, 3.9, 3.10, 3.11, and 3.12
- **Platform**: Ubuntu Latest
- **Steps**:
  1. Checkout code
  2. Set up Python environment
  3. Install system dependencies (libtidy-dev for pytidylib)
  4. Cache pip dependencies for faster builds
  5. Install package and dependencies
  6. Run compatibility tests (no dependencies required)
  7. Run comprehensive test suite
  8. Test package import functionality

##### 2. Build Job (`build`)
- **Dependencies**: Requires test job to pass
- **Platform**: Ubuntu Latest with Python 3.12
- **Steps**:
  1. Checkout code
  2. Set up Python environment
  3. Install build dependencies (build, twine)
  4. Build source and wheel distributions
  5. Validate package with twine check
  6. Upload build artifacts (retained for 30 days)

##### 3. Lint Job (`lint`)
- **Platform**: Ubuntu Latest with Python 3.12
- **Steps**:
  1. Checkout code
  2. Set up Python environment
  3. Install linting tools (flake8, black, isort)
  4. Run flake8 for syntax errors and basic issues
  5. Check code formatting with black (non-blocking)
  6. Check import sorting with isort (non-blocking)

## Dependency Management

### Dependabot Configuration (`.github/dependabot.yml`)

Automatic dependency updates are configured for:

- **Python Dependencies**: Weekly updates on Mondays at 9:00 AM
  - Monitors `python/sadi.python/setup.py`
  - Opens up to 5 pull requests
  - Labels: `dependencies`, `python`

- **GitHub Actions**: Weekly updates on Mondays at 9:00 AM
  - Monitors workflow files in `.github/workflows/`
  - Opens up to 3 pull requests
  - Labels: `dependencies`, `github-actions`

## Build System

### Modern Python Packaging

The project uses modern Python packaging standards:

- **`pyproject.toml`**: Main configuration file with project metadata and tool configurations
- **`setup.py`**: Legacy setup file (maintained for compatibility)
- **`setup.cfg`**: Additional configuration for tools like flake8 and pytest
- **`MANIFEST.in`**: Controls what files are included in source distributions

### Build Tools

- **`python -m build`**: Modern Python build frontend
- **`twine`**: Package upload and validation tool
- **`Makefile`**: Convenient development commands

## Testing Strategy

### Test Levels

1. **Compatibility Tests** (`test_compatibility.py`)
   - No external dependencies required
   - Validates Python 3.12 compatibility
   - Checks for deprecated constructs
   - Always runs first in CI

2. **Comprehensive Tests** (`test_sadi_comprehensive.py`)
   - Requires full dependency installation
   - Tests all SADI functionality
   - Covers RDF processing, serialization, services

3. **Smoke Tests** (`test_smoke.py`)
   - Basic functionality verification
   - Quick validation of core features

4. **Legacy Tests** (`tests.py`)
   - Original test suite
   - Maintained for compatibility

### Test Runner

The `run_tests.py` script provides intelligent test execution:
- Automatically detects available dependencies
- Runs compatibility tests without external dependencies
- Provides clear reporting of test results
- Suitable for both local development and CI

## Local Development

### Setup Commands

```bash
# Install development dependencies
make install-dev

# Run all tests
make test

# Run specific test types
make test-compatibility
make test-comprehensive
make test-smoke

# Code quality
make lint
make format
make check-format

# Build package
make build
make clean
```

### Development Workflow

1. **Fork and Clone**: Fork the repository and clone locally
2. **Install Dependencies**: `make install-dev`
3. **Make Changes**: Implement features or fixes
4. **Test Locally**: `make test` and `make lint`
5. **Format Code**: `make format`
6. **Commit and Push**: Create a pull request

## CI Configuration Details

### Caching Strategy

- **Pip Cache**: Cached based on OS, Python version, and setup.py hash
- **Cache Keys**: Hierarchical fallback for maximum cache hit rate
- **Benefits**: Faster builds, reduced dependency download time

### Security

- **No Secrets Required**: Basic CI runs without authentication
- **Artifact Upload**: Build artifacts are uploaded securely
- **Dependency Updates**: Automated via Dependabot with manual review

### Performance

- **Matrix Builds**: Parallel execution across Python versions
- **Conditional Steps**: Some linting steps are non-blocking
- **Artifact Retention**: 30-day retention for build artifacts

## Troubleshooting

### Common Issues

1. **Build Failures**: Check system dependencies (libtidy-dev)
2. **Test Failures**: Verify Python version compatibility
3. **Import Errors**: Ensure package structure is correct
4. **Linting Issues**: Run `make format` to fix formatting

### Debugging CI

- Check GitHub Actions logs for detailed error messages
- Use `workflow_dispatch` to manually trigger builds
- Review artifact uploads for build outputs
- Check dependency cache for performance issues

## Future Enhancements

### Planned Improvements

- **Code Coverage**: Integration with coverage.py and codecov
- **Documentation**: Automated documentation generation and deployment
- **Performance Testing**: Benchmark testing for performance regressions
- **Security Scanning**: Integration with security vulnerability scanners
- **Release Automation**: Automated PyPI publishing on tags

### Monitoring

- **Build Status**: Monitor via GitHub Actions badge in README
- **Dependency Health**: Regular Dependabot updates
- **Test Results**: Comprehensive test reporting in CI logs