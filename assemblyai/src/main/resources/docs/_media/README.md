# Media Assets

This directory contains images, diagrams, and other media assets used in the AssemblyAI Extension documentation.

## Directory Structure

```
_media/
├── README.md (this file)
├── screenshots/
│   └── (API key screenshots, configuration screenshots, etc.)
├── diagrams/
│   └── (workflow diagrams, architecture diagrams, etc.)
└── icons/
    └── (icons and logos)
```

## Naming Conventions

All media files should follow this naming convention:

```
<PAGE_NAME>_<DESCRIPTION>.<extension>
```

### Examples:
- `obtainingApiKey_dashboard.png` - Screenshot of AssemblyAI dashboard
- `obtainingApiKey_api_key_location.png` - Screenshot showing API key location
- `configuration_extension_settings.png` - Extension configuration screenshot
- `TranscribeCall_workflow_diagram.png` - Workflow diagram for Transcribe Call
- `supportedRequest_architecture.png` - Architecture diagram

## File Formats

### Images
- **Screenshots**: PNG format (preferred for UI screenshots)
- **Diagrams**: PNG or SVG format
- **Icons**: PNG or SVG format

### Recommended Sizes
- **Screenshots**: Maximum width 1200px (will be responsive)
- **Diagrams**: Maximum width 1000px
- **Icons**: 64x64px or 128x128px

## Usage in Documentation

To reference images in markdown files:

```markdown
![Alt Text](_media/filename.png)
```

### Example:
```markdown
![AssemblyAI Dashboard](_media/obtainingApiKey_dashboard.png)
```

## Image Optimization

Before adding images:
1. Optimize file size (compress without quality loss)
2. Use descriptive filenames
3. Ensure images are clear and readable
4. Use consistent styling (borders, shadows, etc.)

## Current Assets

Currently, this directory is empty. Add screenshots and diagrams as needed for documentation enhancement.

### Suggested Assets to Add:

#### Obtaining API Key Page
- `obtainingApiKey_signup.png` - AssemblyAI signup page
- `obtainingApiKey_dashboard.png` - Dashboard after login
- `obtainingApiKey_api_key_location.png` - Where to find API key
- `obtainingApiKey_copy_key.png` - Copying the API key

#### Configuration Page
- `configuration_extension_list.png` - Extension list in Krista
- `configuration_settings.png` - Extension configuration settings
- `configuration_api_key_input.png` - API key input field
- `configuration_save_success.png` - Successful configuration

#### Workflow Diagrams
- `supportedRequest_complete_workflow.png` - Complete transcription workflow
- `supportedRequest_manual_workflow.png` - Manual step-by-step workflow
- `TranscribeCall_process_flow.png` - Transcribe Call process flow

#### Architecture Diagrams
- `overview_architecture.png` - Extension architecture overview
- `overview_integration.png` - Integration with Krista platform

## Maintenance

- Review images periodically for accuracy
- Update screenshots when UI changes
- Remove unused images
- Keep file sizes optimized
- Maintain consistent naming conventions

## Notes

- All images should be relevant to documentation
- Avoid including sensitive information in screenshots
- Use placeholder data in examples
- Ensure images are accessible (provide alt text)
- Consider dark mode compatibility for diagrams

